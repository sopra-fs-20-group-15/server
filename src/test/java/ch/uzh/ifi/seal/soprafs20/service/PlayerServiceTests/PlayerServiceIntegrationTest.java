package ch.uzh.ifi.seal.soprafs20.service.PlayerServiceTests;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
/**
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testPlayer = new PlayerEntity();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");

        // when
        PlayerEntity createdPlayer = playerService.createUser(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(PlayerStatus.OFFLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testPlayer = new PlayerEntity();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");
        PlayerEntity createdPlayer = playerService.createUser(testPlayer);

        // attempt to create second user with same username
        PlayerEntity testPlayer2 = new PlayerEntity();

        // change the name but forget about the username
        testPlayer2.setName("testName2");
        testPlayer2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createUser(testPlayer2));
    }
}*/
