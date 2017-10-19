package org.frameworkset.elasticsearch.client;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.entity.SearchHit;
import org.frameworkset.elasticsearch.entity.SearchResult;
import org.frameworkset.elasticsearch.handler.BaseESResponsehandler;
import org.frameworkset.elasticsearch.serial.ESClassType;
import org.frameworkset.elasticsearch.serial.ESSerialThreadLocal;
import org.frameworkset.elasticsearch.serial.ESTypeReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetDocumentResponseHandler extends BaseESResponsehandler<SearchResult> {
	private static Logger logger = LoggerFactory.getLogger(GetDocumentResponseHandler.class);

	public GetDocumentResponseHandler() {
		// TODO Auto-generated constructor stub
	}

	public GetDocumentResponseHandler(ESTypeReferences<?,?> types) {
		super(types);
	}
	public GetDocumentResponseHandler(ESClassType type) {
		super(type);
	}

	public GetDocumentResponseHandler(Class<?> type) {
		super(type);
	}

	 @Override
     public SearchResult handleResponse(final HttpResponse response)
             throws ClientProtocolException, IOException {
         int status = response.getStatusLine().getStatusCode();

         if (status >= 200 && status < 300) {
             HttpEntity entity = response.getEntity();
			 SearchHit searchResponse = null;
             try {
            	 ESSerialThreadLocal.setESTypeReferences(types);
                 searchResponse = entity != null ? SimpleStringUtil.json2Object(entity.getContent(), SearchHit.class) : null;
//                 String content = EntityUtils.toString(entity);
//                 System.out.println(content);
//                 searchResponse = entity != null ? SimpleStringUtil.json2Object(content, RestResponse.class) : null;
             }
             catch (Exception e){
//                 logger.error("",e);
                 throw new ElasticSearchException(e);
             }
             finally{
            	 ESSerialThreadLocal.clean();
             }
//             ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(TransportClient.class);
//             NamedWriteableRegistry namedWriteableRegistry = (NamedWriteableRegistry)classInfo.getPropertyValue(clientUtil.getClient(),"namedWriteableRegistry");

             return searchResponse;

         } else {
             HttpEntity entity = response.getEntity();
             if (entity != null ) {
				 String content = EntityUtils.toString(entity);
				 SearchHit searchResponse = null;
                 try {
                     searchResponse = entity != null ? SimpleStringUtil.json2Object(content, SearchHit.class) : null;
                 }
                 catch (Exception e){
					 throw new ElasticSearchException(content,e);
                 }
                 return searchResponse;
             }
             else
                 throw new ClientProtocolException("Unexpected response status: " + status);
         }
     }

}