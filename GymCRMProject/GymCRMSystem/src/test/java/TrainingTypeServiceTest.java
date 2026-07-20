import app.entities.TrainingType;
import app.persistence.TrainingTypeRepository;
import app.services.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceTest {


    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    List<TrainingType> trainingTypeList = new ArrayList<>();


    @BeforeEach
    public void setup() {
        TrainingType trainingType = new TrainingType(
                1L, "one", null, null
        );
        for(long i = 0; i < 5; i++) {
            trainingType = new TrainingType(
                    i, "one", null, null
            );
            trainingTypeList.add(trainingType);
        }
    }

    @Test
    public void testGetTrainingTypes() {
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypeList);

        List<TrainingType> result = trainingTypeService.getTrainingTypes();

        assertEquals(5, result.size());
        verify(trainingTypeRepository).findAll();
    }

    @Test
    public void testGetTrainingTypeById() {
        when(trainingTypeRepository.findById(0)).thenReturn(Optional.of(trainingTypeList.get(0)));

        TrainingType result = trainingTypeService.getTrainingTypeById(0);

        assertNotNull(result);
        assertEquals(0, result.getId());
        verify(trainingTypeRepository).findById(0);
    }

}
