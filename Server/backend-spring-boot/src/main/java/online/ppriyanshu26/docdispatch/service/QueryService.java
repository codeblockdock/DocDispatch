package online.ppriyanshu26.docdispatch.service;

import online.ppriyanshu26.docdispatch.dto.AddQueryRequest;
import online.ppriyanshu26.docdispatch.dto.AttendQueryRequest;
import online.ppriyanshu26.docdispatch.dto.QueryResponse;
import online.ppriyanshu26.docdispatch.entity.Attended;
import online.ppriyanshu26.docdispatch.entity.Query;
import online.ppriyanshu26.docdispatch.repository.AttendedRepository;
import online.ppriyanshu26.docdispatch.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {
    
    private final QueryRepository queryRepository;
    private final AttendedRepository attendedRepository;
    
    @Autowired
    public QueryService(QueryRepository queryRepository, AttendedRepository attendedRepository) {
        this.queryRepository = queryRepository;
        this.attendedRepository = attendedRepository;
    }
    
    @Transactional
    public void addQuery(AddQueryRequest request) {
        Query query = new Query();
        query.setContact(request.getContact());
        query.setName(request.getName());
        query.setAge(request.getAge());
        query.setGender(request.getGender());
        query.setTemperature(request.getTemperature());
        query.setDays(request.getDays());
        query.setContagious(request.getContagious());
        query.setAttended(0);
        
        queryRepository.save(query);
    }
    
    public List<QueryResponse> getQueriesByContact(String contact) {
        List<Query> queries = queryRepository.findByContact(contact);
        List<QueryResponse> responses = new ArrayList<>();
        
        for (Query query : queries) {
            QueryResponse response = new QueryResponse();
            response.setQid(query.getQid());
            response.setContact(query.getContact());
            response.setAttended(query.getAttended());
            
            if (query.getAttended() == 1) {
                attendedRepository.findByQid(query.getQid()).ifPresent(attended -> {
                    response.setDoctor(attended.getDoctor());
                    response.setTreatment(attended.getTreatment());
                    response.setRemarks(attended.getRemarks());
                    response.setAttendedAt(attended.getAttendedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                });
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    @Transactional
    public void attendQuery(AttendQueryRequest request) {
        // Update query as attended
        Query query = queryRepository.findById(request.getQid())
            .orElseThrow(() -> new RuntimeException("Query not found"));
        query.setAttended(1);
        queryRepository.save(query);
        
        // Add attended record
        Attended attended = new Attended();
        attended.setQid(request.getQid());
        attended.setContact(request.getContact());
        attended.setDoctor(request.getDoctor());
        attended.setTreatment(request.getTreatment());
        attended.setRemarks(request.getRemarks() != null ? request.getRemarks() : "");
        
        attendedRepository.save(attended);
    }
}
