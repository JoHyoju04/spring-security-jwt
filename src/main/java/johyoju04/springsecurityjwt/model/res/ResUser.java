package johyoju04.springsecurityjwt.model.res;

import lombok.Getter;

@Getter
public class ResUser {
    private final Long id;
    private final String email;

    private ResUser(Long id,String email){
        this.id = id;
        this.email = email;
    }

    public static ResUser of(Long id,String email){
        return new ResUser(id,email);
    }
}
