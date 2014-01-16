package com.openMap1.mapper.writer;


import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.structures.*;

/* class to find an XML structure definition,
currently from an XML Schema or a MIF or a Relax NG file
*/
/**
 * class with static methods to find an XML structure definition,
 * currently from an XML Schema or a MIF or a Relax NG file
 *
 * @author Robert Worden
 * @version 1.0
 */
public class structureGetter {

        /**
         * constructor is not used
         */
        public structureGetter() {
  }

    /**
     * read in the recursive definition of an XML structure from an XML schema, MIF or Relax NG,
     * then construct a tree breadth-first from it with a maximum number of nodes.
     *
     * @param StructureFileName String the name of the file defining XML; must hav extensiton .xsd, .mif  or .rng
     * @param maxNodes int the maximum number of nodes to be made in a breadth-first expansion of the XML structure tree
     * @param MDLBase definition of output mappings. If not null, cut off the tree at any subtree which has no mappings
     * @param testing boolean if true, write some detailed trace iinformation
     * @return XSDStructure the tree structure definition
     * @throws XMLException invalid file extension, or other XML file problem
     * @throws MDLReadException
     */
    public static XSDStructure findStructure(String StructureFileName, MDLBase md, int maxNodes, boolean testing) throws XMLException, MDLReadException
  {
      XSDStructure xs = null;
      /*
      XMLSchema xsd;

        xs = null;
        if (GenUtil.contains(StructureFileName,".xsd"))
        {
            xsd = new XMLSchema(StructureFileName,testing);
            xsd.setMaxNodes(maxNodes); // may override the default 10000
            xsd.captureXSD(md);
            xs = xsd;
            GenUtil.message("Read in XML Schema from '" + StructureFileName + "';");
        }
        else {throw new XMLException("Name of file defining XML structure should end in '.xsd' or '.rng'");
        }
        */
     return xs;
  }


}
