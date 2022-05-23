package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.*;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public PathService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(long source, long target, int age) {
        Sections sections = new Sections(sectionDao.findAll());
        Stations stations = new Stations(stationDao.findAll());
        Lines lines = new Lines(lineDao.findAll());
        Path path = new Path(stations, sections, lines);

        List<Station> shortestPath = path.calculateShortestPath(source, target);
        int shortestDistance = path.calculateShortestDistance(source, target);
        int extraFare = path.calculateExtraFare(source, target);
        List<StationResponse> stationResponses = createStationResponses(shortestPath);

        return new PathResponse(stationResponses, shortestDistance, FareCalculator.calculate(shortestDistance, extraFare, age));
    }

    private List<StationResponse> createStationResponses(List<Station> shortestPath) {
        return shortestPath.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
