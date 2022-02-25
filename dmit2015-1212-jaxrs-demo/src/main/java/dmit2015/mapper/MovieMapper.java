package dmit2015.mapper;

import dmit2015.dto.MovieDto;
import dmit2015.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper( MovieMapper.class );

    MovieDto toDto(Movie entity);

    Movie toEntity(MovieDto dto);

}