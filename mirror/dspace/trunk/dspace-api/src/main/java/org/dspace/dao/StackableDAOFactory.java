package org.dspace.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.PluginManager;

public class StackableDAOFactory
{
    private static Logger log = Logger.getLogger(StackableDAOFactory.class);
    
    public static StackableDAO getInstance(StackableDAO dao, Context context)
    {
        StackableDAO cached = context.fromCache(dao.getClass(), -1);
        if (cached != null)
        {
            return cached;
        }

        StackableDAO instantiated = null;
        try
        {
            instantiated = dao.getClass().getConstructor(Context.class).newInstance(context);
        }
        catch (InstantiationException e)
        {
            log.error(e);
        }
        catch (IllegalAccessException e)
        {
            log.error(e);
        }
        catch (InvocationTargetException e)
        {
            log.error(e);
        }
        catch (NoSuchMethodException e)
        {
            log.error(e);
        }
        context.cache(instantiated, -1);
        return instantiated;
    }

    public static <T extends StackableDAO> T prepareStack(Context context,
            Class clazz, T first, T last, String configLine)
    {
        if (first == null || last == null)
        {
            throw new IllegalArgumentException(
                    "DAO stack must contain at least two elements");
        }

        List<T> list = new ArrayList<T>();

        list.add(first);
        if (ConfigurationManager.getBooleanProperty(configLine))
        {
            Object[] hooks = PluginManager.getPluginSequence(clazz);
            for (Object dao : hooks)
            {
                list.add((T) getInstance((T) dao, context));
            }
        }
        list.add(last);

        for (int i = 0; i < list.size() - 1; i++)
        {
            T dao = list.get(i);
            dao.setChild(list.get(i+1));
        }

        return first;
    }
}
