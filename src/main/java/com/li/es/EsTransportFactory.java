package com.li.es;

import com.li.util.HttpClientUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by wangdi on 18/5/2.
 */
public class EsTransportFactory {
    private static final Logger log = LoggerFactory.getLogger(EsTransportFactory.class);

    public  TransportClient transportClient=null;
    public BulkRequestBuilder bulkRequest=null;
    private String esTable;
    private String dateTime;

    public EsTransportFactory(){
        this.transportClient = createConn();
        this.bulkRequest = transportClient.prepareBulk();
        this.bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
    }

    public  TransportClient createConn(){
        try{

            Settings settings = Settings.builder().put("cluster.name", "data-intelligence")
                    .put("client.transport.sniff", true)
                    .build();
            transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.38"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.38"), 9301))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.39"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.39"), 9301))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.41"), 9300))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("10.18.70.41"), 9301));
            return transportClient ;
        }catch (Exception e){
            log.error("EsTransportFactory init excpetion",e);
        }
        return null;
    }

    public void init(){
        this.transportClient = createConn();
        this.bulkRequest = transportClient.prepareBulk();
        this.bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
    }

    public EsTransportFactory(String esTable, String dateTime)  {
        this.esTable =esTable;
        this.dateTime = dateTime;
        createConn();
    }

    public BulkRequestBuilder getBulkRequest(){
        return transportClient.prepareBulk();
    }





    public void close() {
        transportClient.close();
    }

    /**
     * 删除文档
     * @param
     * @return
     */
    public void deleteDocument(String dateTime){
        String deletebyquery = "{\"query\": {\"match\": {\"dt\":\""+dateTime+"\" }}}";
        String url = "http://10.18.70.38:9200/"+esTable+"/_delete_by_query";
        /*DeleteByQueryResponse response =  new DeleteByQueryRequestBuilder(transportClient,
                DeleteByQueryAction.INSTANCE)
                .setIndices(esTable)
                .setTypes("my_type")
                .setSource(deletebyquery)
                .execute()
                .actionGet();*/

        try{
            HttpClientUtils.reqPost(url,deletebyquery);
        }catch ( Exception e){
            log.error("deleteDocument exception",e);
        }


    }

    public TransportClient getTransportClient() {
        return transportClient;
    }


    public void setBulkRequest(BulkRequestBuilder bulkRequest) {
        this.bulkRequest = bulkRequest;
    }

    public static void main(String[] args) {
        try{


            EsTransportFactory factory = new EsTransportFactory("t_fact_tracking_charge","20180515");
            factory.deleteDocument("20180515");
            factory.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
