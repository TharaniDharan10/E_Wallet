package org.example.userservice.constants;
//WE CREATED THIS FOR KEY NAMES TO SEND TO KAFKA
public interface UserCreationTopicConstants {   //either we can make this as class and make public static final in all keys (or) make it an interface so that we need not make public static final
    String NAME = "NAME";

    String EMAIL = "EMAIL";

    String PHONENO = "PHONENO";

    String USERID = "USERID";
}
