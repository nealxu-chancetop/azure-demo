package service;

import com.azure.cosmos.models.SqlQuerySpec;
import core.framework.cosmos.CosmosCollection;
import core.framework.inject.Inject;
import domain.BOMHeader;
import domain.Item;
import domain.ItemStatus;
import domain.StorageDimensionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Neal
 */
public class ItemService {
    private final Logger logger = LoggerFactory.getLogger(ItemService.class);
    @Inject
    CosmosCollection<Item> itemCollection;
    @Inject
    CosmosCollection<BOMHeader> bomHeaderCollection;

    public void simpleCURDTest() {
        Item item = new Item();
        String id = UUID.randomUUID().toString();
        item.id = id;
        item.searchName = "neal-test-item1";
        item.name = "neal-test-item1";
        item.storageDimensionGroup = StorageDimensionGroup.WMS_COMPPP;
        item.status = ItemStatus.NEW;
        item.restaurantIds = List.of("r1", "r2", "r3", "r4");
        Item.UnitConversion u1 = new Item.UnitConversion();
        u1.fromQuantity = 1d;
        u1.fromUnit = "g";
        u1.toQuantity = 2d;
        u1.toUnit = "ea";
        Item.UnitConversion u2 = new Item.UnitConversion();
        u2.fromQuantity = 20d;
        u2.fromUnit = "cup";
        u2.toQuantity = 5000d;
        u2.toUnit = "g";
        item.unitConversions = List.of(u1, u2);
        ZonedDateTime now = ZonedDateTime.now();
        item.createdTime = now.toEpochSecond();
        item.updatedBy = "neal";
        item.updatedTime = now.toEpochSecond();
        itemCollection.insert(item);
        logger.info("insert");

        item = itemCollection.get(id).orElseThrow();

        item.updatedBy = "cosmos-test";
        item.updatedTime = ZonedDateTime.now().toEpochSecond();
        itemCollection.upsert(item);
        itemCollection.delete(id);
    }

    public void initTestData() {
        ZonedDateTime now = ZonedDateTime.now();

        Item item1 = new Item();
        item1.id = "500001";
        item1.searchName = "test";
        item1.name = "test1";
        item1.storageDimensionGroup = StorageDimensionGroup.WMS;
        item1.status = ItemStatus.NEW;
        item1.restaurantIds = List.of("r1", "r2");
        Item.UnitConversion u1 = new Item.UnitConversion();
        u1.fromQuantity = 1d;
        u1.fromUnit = "g";
        u1.toQuantity = 2d;
        u1.toUnit = "ea";
        item1.unitConversions = List.of(u1);
        item1.createdTime = now.toEpochSecond();
        item1.updatedBy = "neal";
        item1.updatedTime = now.toEpochSecond();

        Item item2 = getItem2(now, u1);

        BOMHeader bomHeader1 = new BOMHeader();
        bomHeader1.id = "50000101";
        bomHeader1.name = "bom1";
        bomHeader1.itemNumber = "500000";

        BOMHeader.BomLine bomLine1 = new BOMHeader.BomLine();
        bomLine1.itemNumber = "500001";
        bomLine1.quantity = 1d;
        bomLine1.unit = "ea";
        bomLine1.order = 1;
        bomLine1.manageInventory = Boolean.TRUE;

        BOMHeader.BomLine bomLine2 = new BOMHeader.BomLine();
        bomLine2.itemNumber = "500002";
        bomLine2.quantity = 2d;
        bomLine2.unit = "cup";
        bomLine2.order = 2;
        bomLine2.manageInventory = Boolean.FALSE;
        bomHeader1.bomLines = List.of(bomLine1, bomLine2);
        bomHeader1.createdBy = "neal";
        bomHeader1.createdTime = now.toEpochSecond();

        itemCollection.upsert(item1);
        itemCollection.upsert(item2);
        bomHeaderCollection.upsert(bomHeader1);

    }

    private Item getItem2(ZonedDateTime now, Item.UnitConversion u1) {
        Item item2 = new Item();
        item2.id = "500002";
        item2.searchName = "test";
        item2.name = "test2";
        item2.storageDimensionGroup = StorageDimensionGroup.WMS_SITEPP;
        item2.status = ItemStatus.PUBLISHED;
        item2.restaurantIds = List.of("r2", "r3");
        Item.UnitConversion u2 = new Item.UnitConversion();
        u2.fromQuantity = 1d;
        u2.fromUnit = "g";
        u2.toQuantity = 2d;
        u2.toUnit = "ea";
        item2.unitConversions = List.of(u1, u2);
        item2.createdTime = now.toEpochSecond();
        item2.updatedBy = "neal";
        item2.updatedTime = now.toEpochSecond();
        return item2;
    }

    public void testSearch() {
        //select id
        logger.info("id");
        itemCollection.get("500001").ifPresent(item -> {
            logger.info("get by id, {}", item.name);
        });

        itemCollection.findOne(new SqlQuerySpec("select * from c where c.id = '500001'")).ifPresent(item -> {
            logger.info("get by find one, {}", item.name);
        });

        //select name like
        logger.info("name like");
        itemCollection.find(new SqlQuerySpec("select * from c where CONTAINS(c.name,'test',false) ORDER BY c.createdTime DESC")).forEach(item -> {
            logger.info(item.name);
        });

        //select inner list
        logger.info("select inner list");
        //can't use  SELECT * FROM c.restaurantIds ORDER BY c.createdTime DESC
        itemCollection.find(new SqlQuerySpec("SELECT * FROM c.restaurantIds"), List.class).forEach(list -> {
            logger.info("{}, size:{}", list, list.size());
        });

        //group by   don't support!!!!
//        logger.info("group by");
//        itemCollection.select("SELECT c.status FROM c GROUP BY c.status", JsonNode.class).forEach(node -> {
//            logger.info("{} - {}", node.get("status").asText(), node.get("total").asLong());
//        });

        test2();
    }

    private void test2() {

        //offset must Min(0)
        logger.info("offset limit");
        itemCollection.find(new SqlQuerySpec("SELECT * from c OFFSET 0 LIMIT 1")).forEach(item -> {
            logger.info("id:{}  name:{}", item.id, item.name);
        });

        //select arrays[:index]
        logger.info("select arrays[:index]");
        bomHeaderCollection.find(new SqlQuerySpec("Select * from c WHERE c.bomLines[0].itemNumber = '500001'")).forEach(bomHeader -> {
            logger.info("id:{}  name:{}", bomHeader.id, bomHeader.name);
        });

        //special char
        logger.info("special char");
        bomHeaderCollection.find(new SqlQuerySpec("SELECT * FROM c WHERE c['name'] = 'bom1'")).forEach(bomHeader -> {
            bomHeader.bomLines.forEach(bomLine -> logger.info("line order:{} , item_number:{}", bomLine.order, bomLine.itemNumber));
        });
    }
}
