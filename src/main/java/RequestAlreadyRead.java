import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;

public class RequestAlreadyRead extends AbstractVerticle {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions().setClustered(false);
        Vertx.vertx(options).deployVerticle(new RequestAlreadyRead());
    }

    @Override
    public void start() throws Exception {
        super.start();
        Router router = Router.router(this.vertx);
        // wait for a payload at this url
        router.put("/test").handler(routingContext -> {
            // first query another service
            this.vertx.createHttpClient().get(8080, "localhost", "/test", clientResponse -> {
                // read the response of the other service
                clientResponse.bodyHandler(responseBody -> {
                    if(clientResponse.statusCode() != 200) {
                        routingContext.response().setStatusCode(500).end("cannot reach client");
                        return;
                    }
                    // now read the body of the request and process it
                    routingContext.request().bodyHandler(requestBody ->
                        routingContext.response().setStatusCode(200).end(responseBody)
                    );
                });
            }).end();
        });
        router.get("/test").handler(routingContext -> routingContext.response().setStatusCode(200).end("something"));
        this.vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
