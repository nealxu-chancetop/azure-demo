package item.service;

import app.cosmos.api.item.ItemWebService;
import core.framework.inject.Inject;
import service.ItemService;

/**
 * @author Neal
 */
public class ItemWebServiceImpl implements ItemWebService {
    @Inject
    ItemService itemService;

    @Override
    public void init() {
        itemService.initTestData();
    }

    @Override
    public void search() {
        itemService.testSearch();
    }
}
