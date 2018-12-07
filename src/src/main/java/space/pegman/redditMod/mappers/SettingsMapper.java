package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettingsMapper {

    @Select("SELECT key AS key, value AS value FROM Settings WHERE key = #{key};")
    String getSetting(String key);

    @Insert("INSERT OR REPLACE INTO Settings (key, value) VALUES (#{key}, #{value});")
    void setSetting(@Param("key") String key, @Param("value") String value);

}
