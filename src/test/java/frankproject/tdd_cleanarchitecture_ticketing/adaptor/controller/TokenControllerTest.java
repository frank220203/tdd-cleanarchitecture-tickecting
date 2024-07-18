package frankproject.tdd_cleanarchitecture_ticketing.adaptor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.usecase.TokenUsecase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenUsecase tokenUsecase;

    @Test
    public void generateNewToken_success() throws Exception {
        // Mock 데이터 설정
        long customerId = 1L;
        long concertId = 1L;
        TokenDTO mockTokenDTO = new TokenDTO(1L, concertId, customerId, 1L, "PENDING", null, null);

        // 요청 바디 설정
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("customerId", customerId);
        requestMap.put("concertId", concertId);

        // Mocking 행위 설정
        when(tokenUsecase.generateNewToken(anyLong(), anyLong())).thenReturn(mockTokenDTO);

        // POST 요청 실행 및 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/api/tokens/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.concertId").value(concertId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void checkToken_success() throws Exception {
        // Mocking tokenUsecase의 checkToken 메서드
        TokenDTO mockTokenDTO = new TokenDTO(1L, 1L, 1L, 1L, "ACTIVE", null, null);
        when(tokenUsecase.checkToken(anyLong(), anyLong())).thenReturn(mockTokenDTO);
        when(tokenUsecase.isActiveToken(anyLong())).thenReturn(true);

        // HTTP GET 요청 보내기
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tokens/check")
                        .param("customerId", "1")
                        .param("concertId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "1")) // Authorization 헤더 추가
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tokenId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.concertId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
    }

}
