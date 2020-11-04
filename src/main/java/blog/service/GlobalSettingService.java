package blog.service;

import blog.dto.responseDto.GlobalSettingDto;
import blog.model.GlobalSetting;
import blog.model.enums.GlobalSettings;
import blog.repository.GlobalSettingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GlobalSettingService
{
    private GlobalSettingRepository globalSettingRepository;



    public GlobalSettingDto getGlobalSetting(){
        List<GlobalSetting> globalSettingsList = globalSettingRepository.findAll();
        GlobalSettingDto globalSettingDto = new GlobalSettingDto();
        globalSettingsList.forEach(globalSetting -> {
            if (globalSetting.getCode().equals(GlobalSettings.MULTIUSER_MODE)){
                globalSettingDto.setMultiUserMode(globalSetting.getValue());
            }
            if (globalSetting.getCode().equals(GlobalSettings.POST_PREMODERATION)){
                globalSettingDto.setPostPremoderation(globalSetting.getValue());
            }
            if (globalSetting.getCode().equals(GlobalSettings.STATISTICS_IS_PUBLIC)){
                globalSettingDto.setStatisticIsPublic(globalSetting.getValue());
            }
        });
        return globalSettingDto;

    }


    public void addNewSetting(GlobalSettingDto globalSettingDto){

        globalSettingRepository
                .updateValue(globalSettingDto.isMultiUserMode(), String.valueOf(GlobalSettings.MULTIUSER_MODE));
        globalSettingRepository
                .updateValue(globalSettingDto.isPostPremoderation(), String.valueOf(GlobalSettings.POST_PREMODERATION));
        globalSettingRepository
                .updateValue(globalSettingDto.isStatisticIsPublic(),
                        String.valueOf(GlobalSettings.STATISTICS_IS_PUBLIC));
    }


    @PostConstruct
    private void postConstruct() {
        List<GlobalSetting> globalSettingList = new ArrayList<>();
        GlobalSetting globalSetting = new GlobalSetting();
        globalSetting.setId(1);
        globalSetting.setCode(GlobalSettings.MULTIUSER_MODE);
        globalSetting.setName("Многопользовательский режим");
        globalSetting.setValue(true);
        globalSettingList.add(globalSetting);

        GlobalSetting globalSetting2 = new GlobalSetting();
        globalSetting2.setId(2);
        globalSetting2.setCode(GlobalSettings.POST_PREMODERATION);
        globalSetting2.setName("Премодерация постов");
        globalSetting2.setValue(true);
        globalSettingList.add(globalSetting2);

        GlobalSetting globalSetting3 = new GlobalSetting();
        globalSetting3.setId(3);
        globalSetting3.setCode(GlobalSettings.STATISTICS_IS_PUBLIC);
        globalSetting3.setName("Показывать всем статистику блога");
        globalSetting3.setValue(true);
        globalSettingList.add(globalSetting3);

        globalSettingRepository.saveAll(globalSettingList);
    }
}
