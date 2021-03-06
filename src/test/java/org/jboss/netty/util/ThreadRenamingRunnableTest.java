/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.util;

import java.security.Permission;
import java.util.concurrent.Executor;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class ThreadRenamingRunnableTest {
  
  
    //@Test(expected = NullPointerException.class)
    @Test
    public void shouldNotAllowNullName() {
        final Runnable runnable = createMock(Runnable.class);
        assertNotNull(runnable);
        try {
        new ThreadRenamingRunnable(
                runnable, 
                null); // thread name
        } catch (Throwable throwable) {
          if (!(throwable instanceof NullPointerException)) {
            throwable.getStackTrace().toString();
          }
          return;
        }
        fail("should throw a NullPointerException");
    }

    //@Test(expected = NullPointerException.class)
    @Test
   public void shouldNotAllowNullRunnable() {
     try {
        new ThreadRenamingRunnable(null, "foo");
        } catch (Throwable throwable) {
          if (!(throwable instanceof NullPointerException)) {
            throwable.printStackTrace();
          }
          return;
        }
        fail("should throw a NullPointerException");
    }

    @Test
    public void testWithoutSecurityManager() throws Exception {
        final String oldThreadName = Thread.currentThread().getName();
        Executor e = new ImmediateExecutor();
        e.execute(new ThreadRenamingRunnable(
                new Runnable() {
                    public void run() {
                        assertEquals("foo", Thread.currentThread().getName());
                        assertFalse(oldThreadName.equals(Thread.currentThread().getName()));
                    }
                }, "foo"));

        assertEquals(oldThreadName, Thread.currentThread().getName());
    }

    @Test
    public void testWithSecurityManager() throws Exception {
        final String oldThreadName = Thread.currentThread().getName();
        Executor e = new ImmediateExecutor();
        System.setSecurityManager(new SecurityManager() {

            @Override
            public void checkAccess(Thread t) {
                throw new SecurityException();
            }

            @Override
            public void checkPermission(Permission perm, Object context) {
                // Allow
            }

            @Override
            public void checkPermission(Permission perm) {
                // Allow
            }
        });
        try {
            e.execute(new ThreadRenamingRunnable(
                    new Runnable() {
                        public void run() {
                            assertEquals(oldThreadName, Thread.currentThread().getName());
                        }
                    }, "foo"));
        } finally {
            System.setSecurityManager(null);
            assertEquals(oldThreadName, Thread.currentThread().getName());
        }
    }

    private static class ImmediateExecutor implements Executor {

        ImmediateExecutor() {
            super();
        }

        public void execute(Runnable command) {
            command.run();
        }
    }

}
