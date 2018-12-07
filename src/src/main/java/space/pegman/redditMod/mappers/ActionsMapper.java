package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Action;


@Mapper
public interface ActionsMapper {

    @Select("SELECT id AS id, name as name, type as type, message as message FROM Actions WHERE id=#{id};")
    Action getActionById(@Param("id") Long id);

    @Select("SELECT id AS id, name as name, type as type, message as message  FROM Actions WHERE type=#{type};")
    Action getActionByType(@Param("type") Integer type);

    @Select("SELECT id AS id, name as name, type as type, message as message FROM Actions;")
    Action[] getActions();

    @Insert("INSERT INTO Actions (name, type, message) VALUES (#{action.name}, #{action.type}, #{action.message});")
    @SelectKey(resultType = Long.class, before=false, keyProperty = "action.id", keyColumn = "id", statement = "SELECT last_insert_rowid() as id;")
    Long insertAction(@Param("action") Action action);

    @Update("UPDATE Actions SET name=#{action.name}, type=#{action.type}, message=#{action.message} where id=#{action.id};")
    Long updateAction(@Param("action") Action action);

    @Delete("DELETE FROM Actions WHERE id=#{id};")
    Long deleteAction(Action action);
}
