package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(final long lineId, final SectionRequest sectionRequest) {
        Sections sections = findSections(lineId);

        sections.add(new Section(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance())
        );

        updateSection(lineId, sections);
    }

    @Transactional
    public void deleteById(final long lineId, final Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.remove(stationId);
        updateSection(lineId, sections);
    }

    private void updateSection(long lineId, Sections sections) {
        sectionDao.deleteByLineId(lineId);
        for (Section section : sections.getSections()) {
            sectionDao.save(lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
        }
    }

    @Transactional(readOnly = true)
    public Sections findSections(final long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        return new Sections(sections);
    }
}
