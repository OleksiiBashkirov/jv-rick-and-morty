package mate.academy.rickandmorty.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaginationInfo(
        @JsonProperty("next") String next
) {
}
