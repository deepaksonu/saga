package com.deepak.UserService.projection;

import com.deepak.CommonService.model.CardDetails;
import com.deepak.CommonService.model.User;
import com.deepak.CommonService.queries.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

    @QueryHandler
    public User getUserPaymentDetails(GetUserPaymentDetailsQuery getUserPaymentDetailsQuery){
        // Ideally get details from db.
        CardDetails cardDetails = CardDetails.builder()
                .name("Deepak Kumar")
                .validUntilYear(2022)
                .validUntilMonth(1)
                .cardNumber("123434534")
                .cvv(123)
                .build();

        return User.builder()
                .userId(getUserPaymentDetailsQuery.getUserId())
                .firstName("Deepak")
                .lastName("Kumar")
                .cardDetails(cardDetails)
                .build();

    }

}
