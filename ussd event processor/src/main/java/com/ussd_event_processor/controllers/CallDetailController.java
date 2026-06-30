package com.ussd_event_processor.controllers;

import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.mapper.CdrMapper;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/calldetail")
public class CallDetailController {
    private final CallDetailRecordRepository callDetailRepository;
    private final CdrMapper cdrMapper;

    public CallDetailController(CallDetailRecordRepository callDetailRepository, CdrMapper cdrMapper) {
        this.callDetailRepository = callDetailRepository;
        this.cdrMapper = cdrMapper;
    }

    @PostMapping("/raw")
    public CallDetailRecord saveRawRecord(@RequestBody Map<String, String> payload) {
        String rawLine = payload.get("line");
        String fileName = payload.getOrDefault("fileName", "manual_upload.log");
        CallDetailRecord record = cdrMapper.mapToEntity(rawLine, fileName);
        return callDetailRepository.save(record);
    }

    @GetMapping
    public Map<String, Object> getAllRecords(){
        return Map.of("records", callDetailRepository.findAll(), "count", callDetailRepository.count());
    }

    @GetMapping("/{id}")
    public CallDetailRecord getRecordById(@PathVariable UUID id){
        return callDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CallDetailRecord not found: " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable UUID id){
        callDetailRepository.deleteById(id);
    }
}