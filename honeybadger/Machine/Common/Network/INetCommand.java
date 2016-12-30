package Machine.Common.Network;

/**
 * Interface for all network messages going back and forth between desktop and rpi
 */
public interface INetCommand {
    void Execute(Object context);
}
