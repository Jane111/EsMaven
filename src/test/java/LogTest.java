
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LogTest {

    public static void main(String[] args) {
       
    	Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
  
        logger.trace("trace level");  
        logger.debug("debug level");  
        logger.info("info level");  
        logger.warn("warn level");  
        logger.error("error level");
        logger.fatal("fatal level");
           
  /*      输出logger时可以看到只有error和fatal级别的被输出来，
        是因为没有配置文件就使用默认的，
        默认级别是error，所以只有error和fatal输出来*/
    }
}



