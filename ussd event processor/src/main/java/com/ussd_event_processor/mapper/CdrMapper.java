package com.ussd_event_processor.mapper;

import com.ussd_event_processor.entity.CallDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@Slf4j
public class CdrMapper {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    public CallDetailRecord mapToEntity(String line, String fileName) {
        String[] fields = line.split("\\|", -1);

        if (fields.length < 33) {
            throw new IllegalArgumentException("Invalid number of fields: " + fields.length);
        }

        CallDetailRecord record = new CallDetailRecord();

        try {
            record.setEventTimestamp(parseTimestamp(fields[0].trim()));
            record.setLac(parseInteger(fields[1]));
            record.setCellId(parseInteger(fields[2]));
            record.setEventType(parseInteger(fields[3]));
            record.setServiceType(parseInteger(fields[4]));

            record.setOriginatingMsisdn(fields[5].trim());
            record.setOptionalField(fields[6].trim());
            record.setProtocolVersion(parseInteger(fields[7]));
            record.setStatusCode1(parseInteger(fields[8]));
            record.setStatusCode2(parseInteger(fields[9]));

            record.setDestinationMsisdn(fields[10].trim());
            record.setUssdString(fields[11].trim());
            record.setFlag1(parseInteger(fields[12]));
            record.setFlag2(parseInteger(fields[13]));

            record.setMsisdn(fields[14].trim());
            record.setFlag3(parseInteger(fields[15]));
            record.setMccMnc(parseInteger(fields[16]));
            record.setImsi(fields[17].trim());

            record.setFlag4(parseInteger(fields[18]));
            record.setFlag5(parseInteger(fields[19]));
            record.setThirdPartyMsisdn(fields[20].trim());

            record.setReserved1(fields[21].trim());
            record.setReserved2(fields[22].trim());
            record.setReserved3(fields[23].trim());
            record.setReserved4(fields[24].trim());

            record.setResult(fields[25].trim());
            record.setSessionType(fields[26].trim());

            LocalDateTime parsedRecordDate = parseTimestamp(fields[27].trim());
            record.setRecordDate(parsedRecordDate != null ? parsedRecordDate : record.getEventTimestamp());

            record.setDurationMs(parseLong(fields[28]));
            record.setBytesSent(parseLong(fields[29]));
            record.setBytesReceived(parseLong(fields[30]));

            record.setMetrics(fields[31].trim());
            record.setTransactionId(fields[32].trim());

            record.setFileName(fileName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to map fields", e);
        }

        return record;
    }

    public LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(timestampStr.trim(), TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public Integer parseInteger(String value) {
        return value == null || value.trim().isEmpty() ? null : Integer.parseInt(value.trim());
    }

    public Long parseLong(String value) {
        return value == null || value.trim().isEmpty() ? null : Long.parseLong(value.trim());
    }
}