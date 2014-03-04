package jp.go.aist.six.vuln.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.go.aist.six.util.IoUtil;
import jp.go.aist.six.util.xml.XmlMapper;


/**
 * @author  Akihito Nakamura, AIST
 * @version $Id: CveXmlMapperImplTest.java 604 2013-06-12 08:14:04Z nakamura5akihito@gmail.com $
 */
public abstract class XmlTestBase
{

    private static final String  _UNMARSHALED_FILE_PREFIX_ = "unmarshalled_";


    protected XmlMapper  _xml_mapper;
    protected File  _working_dir = null;



    protected XmlTestBase(
                    final SixVulnContext context,
                    final String work_subdir
                    )
    {
        _xml_mapper = context.getXmlMapper();

        String  tmp_dirpath = System.getProperty( "java.io.tmpdir" );
        _working_dir = new File( tmp_dirpath, "six-vuln/" + work_subdir );
        _working_dir.mkdirs();
    }



    protected XmlMapper _getXmlMapper()
    {
        return _xml_mapper;
    }


    private File _getWorkingDir()
    {
        return _working_dir;
    }


    private File _newUnmarshalledXmlFile(
                    final File source_xml_file
                    )
    {
        File  unm_xml_file = new File( _getWorkingDir(), _UNMARSHALED_FILE_PREFIX_ + source_xml_file.getName() );
        return unm_xml_file;
    }


    // ZIP or JAR file
    protected void _testZipXmlMapping(
                    final File source_zip_file
                    )
    throws Exception
    {
        System.out.println( "source zip file: " + source_zip_file );
        ZipFile  zip_file = new ZipFile( source_zip_file );

        try {
            Enumeration<? extends ZipEntry>  zip_entries = zip_file.entries();

//            Collection<File>  source_xml_files = new ArrayList<File>();
            while (zip_entries.hasMoreElements()) {
                ZipEntry  zip_entry = zip_entries.nextElement();
                String  zip_entry_name = zip_entry.getName();
                System.out.println( "zip entry: " + zip_entry_name );
                if (zip_entry_name.endsWith( "/" )) {
                    File  output_dir = new File( _getWorkingDir(), zip_entry_name );
                    output_dir.mkdirs();
                } else if (zip_entry_name.endsWith( ".xml" )) {
                    //read zip entry and write to file
                    File  output_file = new File( _getWorkingDir(), zip_entry_name );
                    InputStream  zip_entry_is = zip_file.getInputStream( zip_entry );
                    IoUtil.copy( zip_entry_is, output_file );
                    _testXmlMapping( output_file );
                }
            }
        } finally {
            try {
                zip_file.close();
            } catch (IOException ex) {
                //ignorable
            }
        }
    }



    protected void _testXmlMapping(
                    final File source_xml_file
                    )
    throws Exception
    {
        long  timestamp_begin, timestamp_end;

        System.out.println( "source file: " + source_xml_file );
        System.out.println( "  length (bytes): " + source_xml_file.length() );

        /* (1) unmarshal */
        timestamp_begin = System.currentTimeMillis();
        Object  obj = _getXmlMapper().unmarshal( new FileInputStream( source_xml_file ) );
        timestamp_end = System.currentTimeMillis();
        System.out.println( "  unmarshalled (ms): " + (timestamp_end - timestamp_begin) );

        /* (2) marshal */
        File  out_xml_file = _newUnmarshalledXmlFile( source_xml_file );
        timestamp_begin = System.currentTimeMillis();
        _getXmlMapper().marshal( obj, new FileWriter( out_xml_file ) );
        timestamp_end = System.currentTimeMillis();
        System.out.println( "  marshalled (ms): " + (timestamp_end - timestamp_begin) );
        System.out.println( "    output file: " + out_xml_file );

        /* (3) unmarshal */
        timestamp_begin = System.currentTimeMillis();
        obj = _getXmlMapper().unmarshal( new FileInputStream( out_xml_file ) );
        timestamp_end = System.currentTimeMillis();
        System.out.println( "  unmarshalled (ms): " + (timestamp_end - timestamp_begin) );
    }

}
//
