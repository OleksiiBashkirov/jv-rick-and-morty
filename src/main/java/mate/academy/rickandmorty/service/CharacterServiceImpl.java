package mate.academy.rickandmorty.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import mate.academy.rickandmorty.dto.external.CharacterListDto;
import mate.academy.rickandmorty.dto.internal.CharacterResponseDto;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import mate.academy.rickandmorty.model.Character;
import mate.academy.rickandmorty.repository.CharacterRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements CharacterService {
    private final CharacterRepository characterRepository;
    private final CharacterMapper characterMapper;
    private final CharacterClient characterClient;
    private final Random random = new Random();
    private int numberOfCharacters;

    @Override
    public CharacterResponseDto getRandomCharacter() {
        Long randomId = random.nextLong(numberOfCharacters);
        Optional<Character> randomCharacter = characterRepository.findById(randomId);
        return characterMapper.toDto(randomCharacter.orElseThrow(() ->
                new EntityNotFoundException("Can't find character by id: " + randomId)));
    }

    @Override
    public List<CharacterResponseDto> search(String name) {
        return characterRepository.findCharacterByNameLikeIgnoreCase("%" + name + "%").stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @PostConstruct
    public void init() {
        String dataFromApi = characterClient.fetchDataFromApi();
        CharacterListDto characterListDto = characterClient.parseResponse(dataFromApi);
        numberOfCharacters = characterListDto.characters().size();
        characterRepository.saveAll(characterListDto.characters().stream()
                .map(characterMapper::toModel)
                .toList());
    }
}
