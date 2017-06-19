package app.controller;

import app.dto.TwoStrings;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;
import app.service.CAService;
import app.service.CRLService;
import app.util.ParameterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crl")
public class CRLController {

    @Autowired
    private CRLService crlService;

    // ---------------------------

    @RequestMapping(value = "/reschedule/{id}", method = RequestMethod.PUT)
    public ResponseEntity rescheduleCA(@RequestBody TwoStrings twoStrings, @PathVariable("id") int id) throws InvalidDataException, EntityNotFoundException {
        crlService.rescheduleCRLExecution(id, twoStrings.string1, twoStrings.string2);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/rescheduleAll", method = RequestMethod.PUT)
    public ResponseEntity rescheduleAll(@RequestBody TwoStrings twoStrings) throws InvalidDataException, EntityNotFoundException {
        crlService.rescheduleCRLExecutionForAll(twoStrings.string1, twoStrings.string2);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/getDefault", method = RequestMethod.GET)
    public ResponseEntity<TwoStrings> getDefault(){
        TwoStrings twoStrings = new TwoStrings(ParameterHelper.getDefaultCron(), ParameterHelper.getDefaultFrequencyDescription());
        return new ResponseEntity<>(twoStrings, HttpStatus.OK);
    }

}
