package com.thoughtworks.go.domain;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.io.File;
import java.util.List;

import com.thoughtworks.go.domain.Material;

public class MaterialTest {

    @Test
    public void shouldCreateMaterialForHgMaterial() throws Exception {
        Material material = Material.create(file("testdata/2.4/hg-material.xml"));
        assertThat(material.getType(), is("HgMaterial"));
        assertThat(material.getUrl(), is("foo/bar"));
        List<Material.Change> changes = material.getChanges();
        assertThat(changes.size(), is(2));
        assertThat(changes.get(0), is(new Material.Change("VGarg", "2010-07-07T13:43:01+05:30", "8d17cce203439ef92f19c5fe035da1a6582454e2", "First File")));
        assertThat(changes.get(1), is(new Material.Change("VGarg", "2010-07-07T13:34:01+05:30", "8d17cce203439ef92f19c5fe035da1a658123456", "Zeroth File")));
    }

    @Test
    public void shouldCreateMaterialForSvnMaterial() throws Exception {
        Material material = Material.create(file("testdata/2.4/svn-material.xml"));
        assertThat(material.getType(), is("SvnMaterial"));
        assertThat(material.getUrl(), is("tingtong"));
        assertThat(material.getUsername(), is(""));
        assertThat(material.getCheckExternals(), is(false));
        List<Material.Change> changes = material.getChanges();
        assertThat(changes.size(), is(1));
        assertThat(changes.get(0), is(new Material.Change("vgarg", "2010-06-14T14:07:58+05:30", "48", "Fixed bug #3478.")));
    }

    @Test
    public void shouldCreateMaterialForDependencyMaterial() throws Exception {
        Material material = Material.create(file("testdata/2.4/pipeline-material.xml"));
        assertThat(material.getType(), is("DependencyMaterial"));
        assertThat(material.getPipelineName(), is("application"));
        assertThat(material.getStageName(), is("dev"));

        List<Material.Change> changes = material.getChanges();
        assertThat(changes.size(), is(1));
        assertThat(changes.get(0), is(new Material.Change(null, "2010-07-07T16:21:41+05:30", "application/54/dev/1", null)));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
