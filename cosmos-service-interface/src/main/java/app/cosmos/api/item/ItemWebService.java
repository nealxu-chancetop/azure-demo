package app.cosmos.api.item;

import core.framework.api.http.HTTPStatus;
import core.framework.api.web.service.POST;
import core.framework.api.web.service.PUT;
import core.framework.api.web.service.Path;
import core.framework.api.web.service.ResponseStatus;

/**
 * @author Neal
 */
public interface ItemWebService {
    @POST
    @Path("/item/init")
    @ResponseStatus(HTTPStatus.CREATED)
    void init();

    @PUT
    @Path("/item")
    void search();
}
