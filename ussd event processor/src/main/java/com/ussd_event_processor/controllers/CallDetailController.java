package com.ussd_event_processor.controllers;


import com.ussd_event_processor.entity.CallDetailRecord;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/calldetail")
public class CallDetailController {
    private final CallDetailRecordRepository callDetailRepository;

    public CallDetailController(CallDetailRecordRepository callDetailRepository) {
        this.callDetailRepository = callDetailRepository;
    }

    @GetMapping
    public Iterable<CallDetailRecord> getAllRecords(){
        return callDetailRepository.findAll();
    }

    @PostMapping
    public CallDetailRecord saveRecord(@RequestBody CallDetailRecord record){
        System.out.println(record);
        return callDetailRepository.save(record);
    }

    @GetMapping("/{id}")
    public CallDetailRecord getRecordById(@PathVariable UUID id){
        return callDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CallDetailRecord not found: " + id));
    }

    @PutMapping("/{id}")
    public CallDetailRecord updateFullRecord(@PathVariable UUID id, @RequestBody CallDetailRecord record){
        return callDetailRepository.findById(id)
                .map(existing -> {
                    existing.setRecordStartDateTime(record.getRecordStartDateTime());
                    existing.setRecordEndDateTime(record.getRecordEndDateTime());
                    existing.setMsisdn(record.getMsisdn());
                    existing.setImsi(record.getImsi());
                    existing.setRawLine(record.getRawLine());
                    return callDetailRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("CallDetailRecord not found: " + id));
    }

    @PatchMapping("/{id}")
    public CallDetailRecord updateRecord(@PathVariable UUID id, @RequestBody CallDetailRecord record){
        return callDetailRepository.findById(id)
                .map(existing -> {
                    existing.setRecordStartDateTime(record.getRecordStartDateTime());
                    existing.setRecordEndDateTime(record.getRecordEndDateTime());
                    existing.setMsisdn(record.getMsisdn());
                    existing.setImsi(record.getImsi());
                    existing.setRawLine(record.getRawLine());
                    return callDetailRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("CallDetailRecord not found: " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable UUID id){
        callDetailRepository.deleteById(id);
    }
}