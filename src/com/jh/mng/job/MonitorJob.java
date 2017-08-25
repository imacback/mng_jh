package com.jh.mng.job;


import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jh.mng.common.Config;
import com.jh.mng.service.order.IOrderService;
import com.jh.mng.util.EntSms;

@Service
public class MonitorJob {

	private static final Logger logger = Logger.getLogger(MonitorJob.class);
	
	@Autowired
	private IOrderService orderService;
	
	@Scheduled(fixedDelay = 1000 * 60 * 60)
	public void job_lastSuccess() {
		logger.info("job_lastSuccess is running");
		
		try {
			String gameInfoIds = Config.get().get("monitor_gameInfoIds");
			int limit = Config.get().getInt("uplimit", 60);
			String sendto = Config.get().get("sendto");
			String alertmessage = Config.get().get("alertmessage");
			
			String[] ids = gameInfoIds.split("\\|");
			String[] warntime = Config.get().get("warntime").split("-");
			
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					
					Calendar calendar = Calendar.getInstance();
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					
					int hour1 = Integer.parseInt(warntime[0]);
					int hour2 = Integer.parseInt(warntime[1]);
					
					if (hour < hour1 || hour > hour2) {
						continue;
					}
					
					if (StringUtils.isNotEmpty(ids[i])) {
						
						String[] idStr = ids[i].split(",");
						
						Long id = Long.parseLong(idStr[0]);
						int flag = Integer.parseInt(idStr[1]);
						
						if (flag == 1) { //开启监控
							int time = orderService.getLastSuccessTime(id);
							logger.info("gameid: " + id + ", lastTime: " + time);
							if (time > limit) {
								logger.info("gameId :" + ids[i] + ", 最后计费时间异常");
								EntSms.SendEmail(sendto, alertmessage+"["+time+">="+limit + ",gameId:" + ids[i], alertmessage+"["+time+">="+limit + ",gameId:" + ids[i]);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("job_lastSuccess error :" + e.getMessage());
			e.printStackTrace();
		}
		
	}
}
