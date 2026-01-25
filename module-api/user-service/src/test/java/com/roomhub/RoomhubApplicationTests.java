package com.roomhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=Y29uZmlnLXNlY3JldC1rZXktZm9yLXJvb21odWItcHJvamVjdC10ZXN0LTMyLWNoYXJhY3RlcnMtaW4tYmFzZTY0")
class RoomhubApplicationTests {

    @Test
    void contextLoads() {
    }

}
