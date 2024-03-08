package johyoju04.springsecurityjwt.service;

import johyoju04.springsecurityjwt.repository.UserRepository;
import johyoju04.springsecurityjwt.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

//    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


//    @Override
//    public User loadUserByUsername(String email){
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException(email));
//    }
}
