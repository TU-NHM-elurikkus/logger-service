/**************************************************************************
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 *
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 ***************************************************************************/

package org.ala.jpa.dao.impl;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.ala.jpa.dao.LogEventDao;
import org.ala.jpa.entity.LogDetail;
import org.ala.jpa.entity.LogEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA DAO implementation
 * 
 * @author waiman.mok@csiro.au
 *
 */
@Repository
@Transactional(readOnly = true)
public class LogEventDaoImpl implements LogEventDao {
	protected static Logger logger = Logger.getLogger(LogEventDaoImpl.class);
	
    private EntityManager em = null;

    /**
     * Sets the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(LogEvent logEvent) {
    	 em.remove(em.merge(logEvent));
	}

	public LogEvent findLogEventById(int id) {
		return em.find(LogEvent.class, id);
	}

	@SuppressWarnings("unchecked")
	public Collection<LogEvent> findLogEvents() {
		return em.createQuery("select p from LogEvent p order by p.id").getResultList();
	}

	@SuppressWarnings("unchecked")
	public Collection<LogEvent> findLogEvents(int startIndex, int maxResults) {
		return em.createQuery("select p from LogEvent p order by p.id")
        	.setFirstResult(startIndex).setMaxResults(maxResults).getResultList();
	}

	@SuppressWarnings("unchecked")
	public Collection<LogEvent> findLogEventsByEmail(String userEmail) {
		return em.createQuery("select p from LogEvent p where p.userEmail = :userEmail order by p.id")
        	.setParameter("userEmail", userEmail).getResultList();
	}

	@SuppressWarnings("unchecked")
	public Collection<LogEvent> findLogEventsByUserIp(String userIp) {
		return em.createQuery("select p from LogEvent p where userIp = :userIp order by p.id")
        	.setParameter("userIp", userIp).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Object[]> getLogEventsCount(int log_event_type_id, String entity_uid, String year) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT le.month, SUM(ld.record_count) FROM log_detail ld");
		sb.append(" INNER JOIN log_event le ON le.id=ld.log_event_id");
		sb.append(" WHERE le.log_event_type_id = " +  log_event_type_id);
		sb.append(" AND ld.entity_uid = \"" + entity_uid + "\"");
		sb.append(" AND le.month LIKE \"" + year + "%\"");		
		sb.append(" GROUP BY ld.entity_uid, le.month");
		sb.append(" ORDER BY le.month");
		
		logger.debug(sb.toString());
		Query q = em.createNativeQuery(sb.toString());
//		q.setParameter(1, log_event_type_id);
//		q.setParameter(2, entity_uid);
		
		return q.getResultList();
	}
	

	private Integer[] toIntegerArray(Object[] numbers){
		int noOfDownloads = 0;
        int noRecordDownloaded = 0;
        if(numbers != null && numbers.length > 0){
	        if(numbers[0] != null){
	        	noOfDownloads = ((Number)numbers[0]).intValue();
	        }
	        if(numbers[1] != null){
	        	noRecordDownloaded = ((Number)numbers[1]).intValue();
	        }
        }
        return new Integer[]{noOfDownloads, noRecordDownloaded};		
	}
	
    /**
     * @see org.ala.jpa.dao.LogEventDao#getRecordCountByEntity(java.lang.String)
     */
    public Integer[] getLogEventsByEntity(String entity_uid, int log_event_type_id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(le.id) as noOfDownloads, SUM(ld.record_count) as noRecordDownloaded FROM log_detail ld");
        sb.append(" INNER JOIN log_event le ON le.id=ld.log_event_id");
        sb.append(" WHERE le.log_event_type_id = " +  log_event_type_id);
        sb.append(" AND ld.entity_uid = \"" + entity_uid + "\"");
        
        logger.debug(sb.toString());
        Query q = em.createNativeQuery(sb.toString());
        Object[] numbers = (Object[]) q.getResultList().get(0);
        
        return toIntegerArray(numbers);
    }

    /**
     * @see org.ala.jpa.dao.LogEventDao#getRecordCountByEntityAndDateRange(java.lang.String, java.util.Date, java.util.Date)
     */
    public Integer[] getLogEventsByEntityAndDateRange(String entity_uid, int log_event_type_id,
            Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(le.id) as noOfDownloads, SUM(ld.record_count) as noRecordDownloaded FROM log_detail ld");
        sb.append(" INNER JOIN log_event le ON le.id=ld.log_event_id");
        sb.append(" WHERE le.log_event_type_id = " +  log_event_type_id);
        sb.append(" AND ld.entity_uid = \"" + entity_uid + "\"");
        sb.append(" AND le.created >= \"" + sdf.format(startDate) + "\"");
        sb.append(" AND le.created <= \"" + sdf.format(endDate) + "\"");
        logger.debug(sb.toString());
        Query q = em.createNativeQuery(sb.toString());
        Object[] numbers = (Object[]) q.getResultList().get(0);
        
        return toIntegerArray(numbers);
    }

    /**
     * @see org.ala.jpa.dao.LogEventDao#getRecordCountByEntityAndMonthRange(java.lang.String, java.lang.String, java.lang.String)
     */
    public Integer[] getLogEventsByEntityAndMonthRange(String entity_uid, int log_event_type_id,
            String startMonth, String endMonth) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(le.id) as noOfDownloads, SUM(ld.record_count) as noRecordDownloaded FROM log_detail ld");
        sb.append(" INNER JOIN log_event le ON le.id=ld.log_event_id");
        sb.append(" WHERE le.log_event_type_id = " +  log_event_type_id);
        sb.append(" AND ld.entity_uid = \"" + entity_uid + "\"");
        sb.append(" AND le.month >= \"" + startMonth + "\"");
        sb.append(" AND le.month <= \"" + endMonth + "\"");
        logger.debug(sb.toString());
        Query q = em.createNativeQuery(sb.toString());
        Object[] numbers = (Object[]) q.getResultList().get(0);
        
        return toIntegerArray(numbers);    
    }

    /**
     * execute SQL statement
     */
	@SuppressWarnings("unchecked")
	public Collection<Object[]> executeNativeQuery(String sql) {
		Query q = em.createNativeQuery(sql);
		return q.getResultList();
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public LogEvent save(LogEvent logEvent) {
		return em.merge(logEvent);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public LogEvent saveLogDetail(int logEventId, LogDetail logDetail) {
		LogEvent logEvent = findLogEventById(logEventId);

        if (logEvent.getLogDetails().contains(logDetail)) {
        	logEvent.getLogDetails().remove(logDetail);
        }

        logEvent.getLogDetails().add(logDetail);        

        return save(logEvent);
	}
}