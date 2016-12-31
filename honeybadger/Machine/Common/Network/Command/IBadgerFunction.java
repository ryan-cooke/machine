package Machine.Common.Network.Command;

import Machine.rpi.HoneybadgerV6;

/**
 * Interface for TextCommands
 */
public interface IBadgerFunction {
    /**
     * Call a function on a badger, wrapped by any implementation of this interface
     * @param badger A working HoneybadgerV6, which is the object of the invocation
     * @param params An array of string parameters to be parsed and passed into the function
     * @return
     */
    boolean Invoke(HoneybadgerV6 badger, String[] params);

    /**
     * Explain the usage of this function and how it gets called.
     * @return A string which explains how this function should be used.
     */
    String Explain();

    /**
     * The EXACT number of parameters this function must be called with.
     * @return integer representing the min params needed to be called.
     */
    int MinimumParameterNum();
}
