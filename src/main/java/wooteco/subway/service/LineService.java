package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        final Line line = new Line(
                lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getExtraFare()
        );
        final Line newLine = lineDao.save(line);

        sectionDao.save(
                newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );

        List<StationResponse> stationResponses = getStationResponsesByLine(newLine);

        return new LineResponse(
                newLine.getId(),
                newLine.getName(),
                newLine.getColor(),
                stationResponses,
                lineRequest.getExtraFare()
        );
    }

    private List<StationResponse> getStationResponsesByLine(Line newLine) {
        Sections sections = new Sections(sectionDao.findAllByLineId(newLine.getId()));
        Set<Long> stationIds = sections.getStations();
        return stationIds.stream()
                .map(stationDao::getById)
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), getStationResponsesByLine(it), it.getExtraFare()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getById(final long id) {
        final Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), getStationResponsesByLine(line), line.getExtraFare());
    }

    @Transactional
    public void update(final long id, final LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor(), lineRequest.getExtraFare()));
    }

    @Transactional
    public void deleteById(final long id) {
        lineDao.deleteById(id);
    }
}
