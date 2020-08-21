package controller;

import core.framework.inject.Inject;
import core.framework.web.Controller;
import core.framework.web.Request;
import core.framework.web.Response;
import service.ItemService;

/**
 * @author Neal
 */
public class ItemController implements Controller {
    @Inject
    ItemService itemService;

    @Override
    public Response execute(Request request) throws Exception {
        itemService.initTestData();
//        itemService.testSelect();
        return Response.empty();
    }
}
