package xyz.thepathfinder.android.test;

import org.junit.Assert;
import org.junit.Test;
import xyz.thepathfinder.android.Path;

public class PathTest {
    @Test
    public void testChildPath() {
        Path path = new Path("/grand/parent/child");
        Assert.assertEquals("/grand/parent/child/baby", path.getChildPath("baby"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPath(){
        Path path = new Path("");
    }

    @Test
    public void testNullPath(){
        Path path = new Path(null);
        Assert.assertEquals("/default", path.getPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidName() {
        Path path = new Path("/hi/there");
        path.getChildPath("la/de");
    }

    @Test
    public void testGetName() {
        Path path = new Path("/time/dime/mine");
        Assert.assertEquals("mine", path.getName());
    }

    @Test
    public void testGetParentPath() {
        Path path = new Path("/time/dime/mine");

        String dime = path.getParentPath();
        Assert.assertEquals("/time/dime", dime);

        Path dimePath = new Path(dime);
        String time = dimePath.getParentPath();
        Assert.assertEquals("/time", time);

        Path timePath = new Path(time);
        String def = timePath.getParentPath();
        Assert.assertEquals("", def);
    }

    @Test
    public void testPathEquals() {
        Path p1 = new Path("/blah/blob");
        Path p2 = new Path("/blah/b1ob");
        Path p3 = new Path("/blah/blob");

        Assert.assertFalse(p1.equals(p2));
        Assert.assertTrue(p1.equals(p3));
    }
}
