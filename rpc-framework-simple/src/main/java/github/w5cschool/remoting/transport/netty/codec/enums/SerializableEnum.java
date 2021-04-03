package github.w5cschool.remoting.transport.netty.codec.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum SerializableEnum {

    KYRO(1, "kyro");

    private final Integer serializableId;

    private final String serializableKey;

}
