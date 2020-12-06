package blog.service;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import blog.dto.responseDto.GlobalSettingDto;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class GlobalSettingServiceTest {

    @Autowired
    private GlobalSettingService globalSettingService;

    @Test
    @SneakyThrows
    public void getGlobalSettings() {
        GlobalSettingDto dto = globalSettingService.getGlobalSetting();
        assertFalse(dto.isMultiUserMode());
    }

    @Test
    @SneakyThrows
    public void addNewSettings() {
        GlobalSettingDto globalSettingDto = new GlobalSettingDto
                (false, false, true);
        globalSettingService.addNewSetting(globalSettingDto);
        GlobalSettingDto dto = globalSettingService.getGlobalSetting();
        assertFalse(dto.isPostPremoderation());
    }
}
