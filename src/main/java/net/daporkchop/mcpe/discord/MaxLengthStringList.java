package net.daporkchop.mcpe.discord;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

public class MaxLengthStringList {
    private final List<String> backing;
    private final int maxLength;
    private final TIntList lengths = new TIntArrayList();

    public MaxLengthStringList(int maxLength, List<String> backing) {
        this.maxLength = maxLength;
        this.backing = backing;
    }

    public MaxLengthStringList(int maxLength)   {
        this(maxLength, new ArrayList<>());
    }

    public void add(String string)  {
        if (string.length() > maxLength)    {
            return;
        }

        int toRemove = countHowManyToRemove(string.length());
        for (int i = 0; i < toRemove; i++)  {
            lengths.removeAt(0);
            backing.remove(0);
        }

        backing.add(string);
        lengths.add(string.length());
    }

    private int countHowManyToRemove(int newLength)  {
        int toReturn = 0, currLength = 0;
        for (int i : lengths.toArray())   {
            currLength += i;
            toReturn++;
            if (currLength >= newLength)    {
                return toReturn;
            }
        }
        return -1;
    }

    public String combine(String merger)    {
        StringBuilder builder = new StringBuilder(totalLength() + (merger.length() * (lengths.size() - 1)));
        for (int i = 0; i < backing.size(); i++)    {
            builder.append(backing.get(i));
            if (i < backing.size() - 1)    {
                builder.append(merger);
            }
        }

        return builder.toString();
    }

    private int totalLength()   {
        int val = 0;
        for (int i : lengths.toArray()) {
            val += i;
        }

        return val;
    }
}
