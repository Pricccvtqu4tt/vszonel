package com.wasoft;

import java.io.InputStream;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import com.wasoft.client.im.ws.wsimport.IMService;
import com.wasoft.client.im.ws.wsimport.IMServiceService;

public class IM {

	private static IMService imService = null;
	
	private String paramFile 	= "ws-im-params.xml";
	private String nsURI		= "http://ws.websocket.wasoft.com/";
	private String localPart	= "IMServiceService";
	
	private String protocol		= "http";
	private String 	wsdl 		= "/portal/services/IMService";
	
	private String 	wsServer 	= "127.0.0.1";
	private int 	wsPort 		= 7001;
	
	private IM(){}
	private IMService getIMService(){
		try{
			IMServiceService ims = new IMServiceService(new URL(getUrl()), new QName(nsURI, localPart));
			return ims.getIMServicePort();
		}
		catch (Exception e){
			return null;
		}
	}
	
	private String getUrl(){
		try{		
			
			JAXBContext context = JAXBContext.newInstance(Params.class);
			
			InputStream is = null;
			try{
				is = this.getClass().getResourceAsStream("/../" + paramFile);
			}
			catch(Exception e){System.err.println("get context file error: " + e.getMessage());}
			if (is == null){
				this.getClass().getResourceAsStream(paramFile);	
			}			
			Unmarshaller um = context.createUnmarshaller();	
			Params params = (Params) um.unmarshal(is);
			
			protocol = params.protocol;
			wsdl	 = params.wsdl;			
			wsServer = params.server;
			wsPort	 = params.port;			
		}
		catch(Exception e)
		{
			System.err.println("JAXB error: " + e.getMessage());			

		}		
		return protocol + "://" + wsServer + ":" + wsPort + wsdl + "?wsdl";
	}
	
	public static IMService getService(){
		if (imService == null){
			imService = new IM().getIMService();
		}
		return imService;
	}
	
	public static void main(String[] args) {
	
		System.out.println(IM.getService().getCurrentTime());
		
		IM.getService().pushCall(1637, "Portal.Test", "{name:'luojun'}");
		
	}
}

@XmlRootElement
class Params{
	@XmlElement
	String protocol;
	@XmlElement
	String wsdl;
	@XmlElement
	String server;
	@XmlElement
	int port;	
}