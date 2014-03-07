package jp.go.aist.six.vuln.core.repository.scap.nvd;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import jp.go.aist.six.util.repository.ObjectNotFoundException;
import jp.go.aist.six.util.repository.QueryResults;
import jp.go.aist.six.vuln.NvdRepositoryQuery;
import jp.go.aist.six.vuln.NvdRepositoryQuery.PatternAndCount;
import jp.go.aist.six.vuln.core.NvdRepositoryTestDataInstaller;
import jp.go.aist.six.vuln.core.SixVulnContext;
import jp.go.aist.six.vuln.model.scap.vulnerability.VulnerabilityType;
import jp.go.aist.six.vuln.repository.scap.nvd.NvdRepository;
import jp.go.aist.six.vuln.repository.scap.nvd.VulnerabilityQueryParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;



/**
 *
 *
 * @author Aki Nakamura (nakamura5akihito@gmail.com)
 * @version 2014/03/06
 */
@RunWith( Enclosed.class )
public class NvdRepositoryImplTest
{

//    private XmlMapper  _xml_mapper;
    private final NvdRepository   _repository;



    public NvdRepositoryImplTest()
    {
//        _xml_mapper = SixVulnContext.Nvd.repository().getXmlMapper();
//        assertThat( "NOT null", _xml_mapper, notNullValue() );

        _repository = SixVulnContext.Nvd.repository().getRepository();
        assertThat( "NOT null", _repository, notNullValue() );
    }



    protected NvdRepository _getRepository()
    {
        return _repository;
    }


    @Before
    public void setUp()
    throws Exception
    {
        NvdRepositoryTestDataInstaller.execute();
    }



    /**
     */
    @RunWith( Theories.class )
    public static class NonexistentId
    extends NvdRepositoryImplTest
    {
        @DataPoints
        public static String[] vulnIds()
        {
            return NvdRepositoryQuery.NONEXISTENT_VULN_IDS;
        }


        @Rule
        public ExpectedException  expectedEx = ExpectedException.none();


        @Theory
        public void testFindVulnerabilityById(
                        final String id
                        )
        {
            expectedEx.expect( ObjectNotFoundException.class );
            _getRepository().findVulnerabilityById( id );
        }

    }
    //



    /**
     */
    @RunWith( Theories.class )
    public static class VulnIdPattern
    extends NvdRepositoryImplTest
    {
        @DataPoints
        public static PatternAndCount[] vulnIdPatternsAndCounts()
        {
            return NvdRepositoryQuery.VULN_ID_PATTERNS_AND_COUNTS;
        }


        @Theory
        public void testFindVulnerabilityById(
                        final PatternAndCount id_pattern_and_count
                        )
        {
            VulnerabilityQueryParams  params = new VulnerabilityQueryParams();
            params.setId( id_pattern_and_count.pattern );
            System.out.println( "query: " + params );

            QueryResults<VulnerabilityType>  results = _getRepository().findVulnerability( params );
            System.out.println( "  result: #entries=" + results.size() );
            assertThat( results.size(), is( id_pattern_and_count.count ) );
            for (VulnerabilityType  v : results.getElements()) {
                System.out.println( "    vulnerability: id=" + v.getId() + ", cve=" + v.getCveId() );
//                assertThat( v.getCveId().substring( 0, 12 ), is( "CVE-2013-000" ) );
            }
        }

    }
    //

}
//
