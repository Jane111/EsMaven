import java.awt.print.Printable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.omg.CORBA.PUBLIC_MEMBER;



public class EsDemo {
	

	@org.junit.Test
	public void test()
	{
		Random rand = new Random();		
		String miyao="abcdefghijklmnopqrstuvwx";
		byte[] key1=miyao.substring(0,8).getBytes();
		    byte[] key2=miyao.substring(8,16).getBytes();
		    byte[] key3=miyao.substring(16,24).getBytes();//读取解密密钥
//		    byte[] key1="a".getBytes();
//		    byte[] key2="b".getBytes();
//		    byte[] key3="c".getBytes();//读取解密密钥
		   
//		    byte[] my;
//		   for(int i=0;i<24;i++)
//		   {
//			   my[0]=(byte)(rand.nextInt(200)+50);
//		   }
		    byte[] mi=miyao.getBytes();
		   byte[] my1="abfghijklmnopqrstuvwxcde".getBytes();
		   byte[] my2="amnopqrstuvwxabcdefghijk".getBytes();
		   byte[] result = new byte[24];
		   for(int i=0;i<24;i++)
		   {
			   result[i] = (byte) (my1[i]^my2[i]^mi[i]);
		   }
		   
		   
		   byte[] test = new byte[24];
		   for(int i=0;i<24;i++)
		   {
			   test[i] = (byte) (my1[i]^my2[i]^result[i]);
			   System.out.println(test[i]); 
			   
		   }
//		   System.out.println((byte)(2)); 
//		   for(int i=0;i<my.length;i++)
//				  
//		   {
//		   System.out.println(my[i]); 
//		  
//		   }
		   for(int i=0;i<key1.length;i++)
		  
			   {
			   System.out.println(key1[i]); 
			   System.out.println(key1[0]^key1[1]); 
			   }
		   System.out.println(""); 
		   for(int i=0;i<key2.length;i++)
				  
		   {
		   System.out.print(key2[i]); 
		   }
		   System.out.println(""); 
		   for(int i=0;i<key3.length;i++)
				  
		   {
		   System.out.print(key3[i]); 
		   }
		   
	}
	
//	增删改查
	@org.junit.Test
	public void test1() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		
		//数据查询
		GetResponse response = client.prepareGet("house","house_item","100").execute().actionGet();
		
		//得到查询到的数据
		System.out.println(response.getSourceAsString());
		
		client.close();	
	}
	
	@org.junit.Test
	public void test2() throws IOException, InterruptedException, ExecutionException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","my-application").build();
		
		//创建访问ES服务器的客户端
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9200));
		
		/*添加内容*/
		
		//包装一个document的内容
		XContentBuilder doc = XContentFactory.jsonBuilder()
				.startObject()
				.field("id","1")
				.field("title","设计模式")//如果实现中文的查询，需要提前在MAPPING里设置中文分词器
				.endObject();
		//添加文档
		IndexResponse response = client.prepareIndex("index","blog","10")
				.setSource(doc).get();
		
		/*删除文档*/
		DeleteResponse response2=client.prepareDelete("index","blog","10").get();
		
		
		/*部分更新文档*/
		UpdateRequest request = new UpdateRequest();
		request.index("index")
				.type("type")
				.id("10")
				.doc(
						XContentFactory.jsonBuilder().startObject()
						.field("title","java设计模式")
						.endObject());
		UpdateResponse response3 = client.update(request).get();
		
		
		/*mget批量查询*/
		MultiGetResponse response4 = client.prepareMultiGet()
				.add("index","blog","1","2")
				.add("lib","user","1","2")
				.get();
		for(MultiGetItemResponse item:response4)
		{
			GetResponse gt = item.getResponse();
			if(gt!=null&&gt.isExists())
			{
				System.out.println(gt.getSourceAsString());
			}
		}
		
		/*批量增加*/
		BulkRequestBuilder bulkBuilder = client.prepareBulk();
		bulkBuilder.add(client.prepareIndex("index","book","3")
						.setSource(
							XContentFactory.jsonBuilder()
							.startObject()
							.field("price","99")
							.endObject()
						)
					);
		bulkBuilder.add(client.prepareIndex("index","book","9")
				.setSource(
					XContentFactory.jsonBuilder()
					.startObject()
					.field("price","99")
					.endObject()
				)
			);
		
		BulkResponse response5 = bulkBuilder.get();

		/*查询删除*/
		BulkIndexByScrollResponse response6 = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(client)
				.filter(QueryBuilders.matchQuery("title", "gongcheng"))
				.source("index")
				.get();
		long counts = response6.getDeleted();//删除成功的条数
		
		/*query查询*/
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		QueryBuilder builder1 = QueryBuilders.matchQuery("interest", "change");
		QueryBuilder builder2 = QueryBuilders.multiMatchQuery("intersest","chamge","tiaowu");//一个field
		//range查询
		QueryBuilder builder3 = QueryBuilders.rangeQuery("birthday").from("1999-01-01").to("1999-02-18");
		
		//prefix查询
		QueryBuilder builder4 = QueryBuilders.prefixQuery("name", "zhao");
		
		//wildcard查询
		QueryBuilder builder5 = QueryBuilders.wildcardQuery("name", "zhao*");
		
		//fuzzy查询
		QueryBuilder builder6 = QueryBuilders.fuzzyQuery("inters", "chagge");//模糊查询，可以过滤掉一些语法错误
		
		//type查询
		QueryBuilder builder7 = QueryBuilders.typeQuery("blog");
		
		QueryBuilder builder8 = QueryBuilders.simpleQueryStringQuery("+change -hejiu");
		
		QueryBuilder builder9 = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("interets", "changge"))
				.mustNot(QueryBuilders.matchQuery("interets", "changge"))
				.should(QueryBuilders.matchQuery("interets", "changge"))
				.filter(QueryBuilders.rangeQuery("birthday").gte("1999-01-01"));
		
		
		SearchResponse sr = client.prepareSearch("index")
				.setQuery(qb)
				.setSize(3).get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			
			Map<String, Object> map = hit.sourceAsMap();
			for(String key:map.keySet()){
				System.out.println(key+map.get(key));
			}
		}
		
		AggregationBuilder agg = AggregationBuilders.max("aggMax").field("age");
		SearchResponse response7 = client.prepareSearch("index").addAggregation(agg).get();
		Max max = response7.getAggregations().get("aggMax");
		System.out.println(max.getValue());//同理可求最小值，总和，平均值
		
		
		System.out.println(response.status());//得到执行的结果，是否成功
		client.close();	
	}

	
	
	/*按价格来筛选*/
	@org.junit.Test
	public void price() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		QueryBuilder builder = QueryBuilders.rangeQuery("price").from("6000").to("10000");
		
		SearchResponse sr = client.prepareSearch("house")
				.setQuery(builder)
				.get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			}
	}
	/*按面积来筛选*/
	@org.junit.Test
	public void meters() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		QueryBuilder builder = QueryBuilders.rangeQuery("meters").from("300").to("400");
		
		SearchResponse sr = client.prepareSearch("house")
				.setQuery(builder)
				.get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			}
	}
	/*按厅室来筛选*/
	@org.junit.Test
	public void zone() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		QueryBuilder builder = QueryBuilders.matchQuery("zone", "2室");
		
		SearchResponse sr = client.prepareSearch("house")
				.setSize(3000)
				.setQuery(builder)
				.get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			}
	}
	/*按区域来筛选*/
	@org.junit.Test
	public void area() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		QueryBuilder builder = QueryBuilders.matchQuery("area", "百步亭");
		
		SearchResponse sr = client.prepareSearch("house")
				.setSize(3000)
				.setQuery(builder)
				.get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			}
	}
	/*搜索小区来筛选*/
	@org.junit.Test
	public void region() throws UnknownHostException
	{
		//指定ES集群
		Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
		//创建访问ES服务器的客户端
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));//端口号为9300，only in above line
		QueryBuilder builder = QueryBuilders.matchQuery("region", "方舟小区");
		
		SearchResponse sr = client.prepareSearch("house")
				.setSize(3000)
				.setQuery(builder)
				.get();
		SearchHits hits = sr.getHits();
		for(SearchHit hit:hits){
			System.out.println(hit.getSourceAsString());
			}
	}


}
