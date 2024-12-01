package hr.adriaticanimation.saf_planner.controllers.test;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/open")
    public String open() {
        return "Open endpoint reached";
    }

    @GetMapping("/closed")
    public String close() {
        return "Closed endpoint reached";
    }
}
