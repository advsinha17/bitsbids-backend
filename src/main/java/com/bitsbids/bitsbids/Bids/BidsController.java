package com.bitsbids.bitsbids.Bids;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bid")
@CrossOrigin
public class BidsController {

    @Autowired
    private BidsService bidsService;

    @GetMapping("/{bidId}")
    public ResponseEntity<Bids> getUser(@PathVariable UUID bidId) {
        return bidsService.getBidById(bidId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create/{productId}")
    public ResponseEntity<?> createBid(@PathVariable UUID productId, @RequestBody Bids bid) {
        System.out.println(productId);
        System.out.println(bid);
        try {
            Bids createdBid = bidsService.addNewBid(productId, bid);
            return ResponseEntity.ok(createdBid);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
