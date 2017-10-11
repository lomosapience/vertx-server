package vertx.web.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public class Server extends AbstractVerticle {

    /**
     * This method is called when the verticle is deployed. It creates a HTTP server and registers a simple request
     * handler.
     * <p/>
     * Notice the `listen` method. It passes a lambda checking the port binding result. When the HTTP server has been
     * bound on the port, it call the `complete` method to inform that the starting has completed. Else it reports the
     * error.
     *
     * @param fut the future
     */
    @Override
    public void start(Future<Void> fut) {

        // Create a router object.
        Router router = Router.router(vertx);

        CorsHandler corsHandler = CorsHandler.create("*");
        corsHandler
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedHeader("Authorization")
                .allowedHeader("Content-Type")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers");

        router.route().handler(corsHandler);

        // Bind "/" to our hello message.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("Hello, Server!");
        });

        router.post("/api/click").handler(this::doClick);
        router.get("/api/count").handler(this::getCount);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void doClick(RoutingContext routingContext) {

        int clickesCount = Clicker.getInstance().getClicksCount();
        Clicker.getInstance().setClicksCount(++clickesCount);
        JsonObject result = new JsonObject();
        result.put("status", "Success");
        result.put("clickes-count", clickesCount);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(result));
    }

    private void getCount(RoutingContext routingContext) {

        int clickesCount = Clicker.getInstance().getClicksCount();
        JsonObject result = new JsonObject();
        result.put("count", clickesCount);
        routingContext
                .response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(result));
    }
}
