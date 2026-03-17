import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.myblog.dao.PostDao;
import com.myblog.model.Post;
import com.myblog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest  
@AutoConfigureMockMvc  
@ActiveProfiles("test") // Используйте профиль, если у вас есть специфическая конфигурация для тестов  
public class PostService {

    @Mock  
    private PostDao postDao;

    @InjectMocks  
    @Autowired  
    private PostService postService; // Теперь инжектируем через @Autowired

    // В этом случае вам не нужно использовать MockitoAnnotations.openMocks(this),
    // так как Spring будет управлять созданием бинов.

    @Test  
    public void testGetPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        Optional<Post> foundPost = postService.getPostById(postId);
        assertTrue(foundPost.isPresent());
        assertEquals(postId, foundPost.get().getId());
    }
}
