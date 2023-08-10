package com.example.gateway.JsonApi;

import com.example.gateway.RatesCollector.model.RatesResponseData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/json_api")
public class JsonController {

    @PostMapping("/current")
    public RatesResponseData postCurrent(@RequestBody ClientRequestDTO clientRequestDTO){
        RatesResponseData =

    }

    @PostMapping("/history")
    public List<RatesResponseData> postHistory(@RequestBody ClientRequestDTO clientRequestDTO){


    }




}
