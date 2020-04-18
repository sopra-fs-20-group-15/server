package ch.uzh.ifi.seal.soprafs20.service.PlayerServiceTests;

/**
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private PlayerEntity testPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testPlayer = new PlayerEntity();
        testPlayer.setId(1L);
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");

        // when -> any object is being save in the playerRepository -> return the dummy testPlayer
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the playerRepository -> return the dummy testPlayer
        PlayerEntity createdPlayer = playerService.createUser(testPlayer);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(PlayerStatus.OFFLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        playerService.createUser(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        Mockito.when(playerRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(null);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createUser(testPlayer));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        playerService.createUser(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        Mockito.when(playerRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createUser(testPlayer));
    }


}*/
