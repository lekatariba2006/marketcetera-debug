package org.marketcetera.spring;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import quickfix.InvalidMessage;

public class JMSFIXMessageConverter implements MessageConverter {
    private static final String FIX_PREAMBLE = "8=FIX";

    boolean serializeToString = true;
    public static final String BYTES_MESSAGE_CHARSET = "UTF-16";

    public JMSFIXMessageConverter() {
	}
    
    
	public boolean isSerializeToString() {
		return serializeToString;
	}


	public void setSerializeToString(boolean serializeToString) {
		this.serializeToString = serializeToString;
	}

	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        quickfix.Message qfMessage = null;
        if(message instanceof TextMessage) {
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("Received JMS msg: "+message, this);
            }
            // todo: handle validation when creating quickfix message
            try {
				qfMessage = new quickfix.Message(((TextMessage)message).getText());
            } catch (InvalidMessage e) {
                // bug #501 - want to log here
                LoggerAdapter.error(MessageKey.ERR0R_JMS_MESSAGE_CONVERSION.getLocalizedMessage()+": "+e.getMessage(), this);
                throw new MessageConversionException(MessageKey.ERR0R_JMS_MESSAGE_CONVERSION.getLocalizedMessage(), e);
			}
        } else if (message instanceof BytesMessage){
            LoggerAdapter.debug("Received JMS msg: "+message, this);
            try {
                BytesMessage bytesMessage = ((BytesMessage)message);
                int length = (int)bytesMessage.getBodyLength();
                byte [] buf = new byte[length];
                bytesMessage.readBytes(buf);

                String possibleString = new String(buf, BYTES_MESSAGE_CHARSET);
                if (possibleString.startsWith(FIX_PREAMBLE)){
                    qfMessage = new quickfix.Message(possibleString);
                }
            } catch (Exception ex){
                // bug #501 - want to log here
                LoggerAdapter.error(MessageKey.ERR0R_JMS_MESSAGE_CONVERSION.getLocalizedMessage()+": "+ex.getMessage(), this);
                throw new MessageConversionException(MessageKey.ERR0R_JMS_MESSAGE_CONVERSION.getLocalizedMessage(), ex);
            }
        } else if (message instanceof ObjectMessage) {
	        return (quickfix.Message)((ObjectMessage)message).getObject();
	    }
        return qfMessage;
	}

    /** Converts from the ORS to the JMS queue format - ie from a FIX Message -> JMS message */
    public Message toMessage(Object message, Session session) throws JMSException, MessageConversionException {
		javax.jms.Message jmsMessage = null;
		if (serializeToString){
			jmsMessage = session.createTextMessage(message.toString());
		} else if (message instanceof Serializable) {
			Serializable serializable = (Serializable) message;
			jmsMessage = session.createObjectMessage(serializable);
		}
		return jmsMessage;
	}


}
