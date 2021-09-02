package com.gateway.greens;

import com.gateway.code.GreensType;
import com.gateway.common.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("greens")
public class GreensController {
    private final GreensServiceFactory clientFactory;

    @GetMapping
    public List<String> get(@RequestParam GreensType type) {
        GreensService client = clientFactory.getClient(type);
        return client.getList();
    }

    @GetMapping("{type}")
    public Product getDetail(@PathVariable GreensType type, @RequestParam String name) {
        GreensService client = clientFactory.getClient(type);
        return client.get(name);
    }
}
