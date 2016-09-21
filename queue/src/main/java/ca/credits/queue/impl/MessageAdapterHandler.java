package ca.credits.queue.impl;


import ca.credits.common.serialize.CodecFactory;
import ca.credits.queue.EventMessage;
import ca.credits.queue.EventProcessor;
import ca.credits.queue.Message;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.util.QueueInfoUtil;
import ca.credits.queue.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MessageListenerAdapter的Pojo
 * <p>消息处理适配器，主要功能：</p>
 * <p>1、将不同的消息类型绑定到对应的处理器并缓存，如将queue+exchange的消息统一交由A处理器来出来</p>
 * <p>2、执行消息的消费分发，调用相应的处理器来消费属于它的消息</p>
 *
 * User: chenwen
 * Date: 2015-10-29
 */
@Slf4j
public class MessageAdapterHandler {
	final CodecFactory codecFactory;

	private ConcurrentMap<String, EventProcessorWrap> epwMap;

	public MessageAdapterHandler(CodecFactory codecFactory) {
		this.epwMap = new ConcurrentHashMap<>();
		this.codecFactory = codecFactory;
	}

	public void handleMessage(EventMessage eventMessage) throws Exception {
		log.debug("Receive an EventMessage: [" + eventMessage + "]");
		// 先要判断接收到的message是否是空的，在某些异常情况下，会产生空值
		if (eventMessage == null || !ValidateUtil.validateQueueInfo(eventMessage.getQueueInfo())) {
			log.warn("Receive an null EventMessage, it may product some errors, and processing message is canceled.");
			return;
		}

        // 解码，并交给对应的EventHandle执行
        EventProcessorWrap eventProcessorWrap = epwMap.get(QueueInfoUtil.getBindKey(eventMessage.getQueueInfo()));

		if (eventProcessorWrap == null) {
			log.warn("Receive an EopEventMessage, but no processor can do it.");
            return;
        }

		eventProcessorWrap.process(codecFactory.deSerialize(eventMessage.getEventData()));
	}

    public void handleMessage(byte[] bytes){
        log.debug("Receive an EventMessage: [" + bytes + "]");

    }

	protected <T extends Message> void add(QueueInfo queueInfo, EventProcessor<T> processor) {
		EventProcessorWrap epw = new EventProcessorWrap<>(processor);
		EventProcessorWrap oldProcessorWrap = epwMap.putIfAbsent(QueueInfoUtil.getBindKey(queueInfo), epw);
		if (oldProcessorWrap != null) {
			log.warn("The processor of this queue and exchange exists, and the new one can't be add");
		}
	}

	protected Set<String> getAllBinding() {
		return epwMap.keySet();
	}

	protected static class EventProcessorWrap<T extends Message> {
		private EventProcessor<T> eep;

		protected EventProcessorWrap(EventProcessor<T> eep) {
			this.eep = eep;
		}

		public void process(Object message) throws Exception{
			if (message instanceof Message) {
				eep.process((T) message);
			}else {
				log.warn("Receive an EopEventMessage, but no message type is not Message.");
			}
		}
	}
}
