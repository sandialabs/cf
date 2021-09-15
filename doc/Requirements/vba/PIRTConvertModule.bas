Attribute VB_Name = "PIRTConvertModule"

'Constants
'PCMM Elements Sheets
Const RankingGuidelinesSheet As String = "Ranking Guidelines"
Const HeaderSheet As String = "Header"
Const AdequacySheet As String = "Adequacy"
Const LevelsSheet As String = "Levels"
Const LevelDifferenceColoringSheet As String = "Level Difference Coloring"

'Generation Configuration
Const ymlExtension As String = ".yml"
Const GLB_INDENT As String = "  "

'Export PIRT configuration to yml file
Sub ExportPIRTExcelToYaml()
    
    'Generation Configuration
    Pathname = Application.ActiveWorkbook.Path
    Filename = Left(Application.ActiveWorkbook.Name, (InStrRev(Application.ActiveWorkbook.Name, ".", -1, vbTextCompare) - 1))
    FilePath = Pathname & "\" & Filename & ymlExtension
    
    'Create Yml File
    Call CreateYmlFile(FilePath)
    
    'adds rgb colors value in cells
    Call AddLevelDifferenceColoringRgbValues(LevelDifferenceColoringSheet)
    
    'exports common content to yml
    Call ExportContentToYml(FilePath, HeaderSheet)
    Call ExportContentToYml(FilePath, AdequacySheet)
    Call ExportContentToYml(FilePath, LevelsSheet)
    Call ExportContentToYml(FilePath, LevelDifferenceColoringSheet)
    
    'exports Ranking Guidelines to yml
    Call ExportRankingGuidelinesToYml(FilePath, RankingGuidelinesSheet)
    
    'deletes rgb colors value from cells
    Call DeleteLevelDifferenceColoringRgbValues(LevelDifferenceColoringSheet)
    
End Sub

'Create the Yaml File to Generate
Private Sub CreateYmlFile(FilePath)

    Filenum = FreeFile
    Open FilePath For Output As #Filenum
    Close #Filenum
    
End Sub

'Export sheets content to yml
Private Sub ExportContentToYml(FilePath, PIRTSheet)

    'Sheet
    Dim Sheet As Worksheet
    Set Sheet = Sheets(PIRTSheet)
    
    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum

    'Variables
    Dim r As Long, c As Long, Name As String, AttributeHeader As String, AttributeValue As String
                 
    'Comments
    Comment = Sheet.Range("A1").Value
    Print #Filenum, "#" & Comment
    
    'Sheetname section
    Print #Filenum, """" & Sheet.Name & """:"
           
    'Fields section
    Print #Filenum, GLB_INDENT & """Fields"":"
    
    Dim rowCount As Long
    rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
    Dim columnCount As Long
    columnCount = Sheet.Cells(2, Sheet.Columns.Count).End(xlToLeft).Column
    
    'Fields parsing
    For r = 3 To rowCount '<- Data excluding headers in A1:B2 (header and comments)
        Name = Sheet.Range("B" & r).Value
        
        'If Name is Empty do not print attribute
        If Name <> "" Then
            'Append ':' char if there is attributes on fields
            If columnCount > 2 Then
                Name = """" & Name & """:"
            Else
                Name = "- """ & Name & """"
            End If
            
            Print #Filenum, GLB_INDENT & GLB_INDENT & Name
            
            'Parse field attributes
            For c = 3 To columnCount
                AttributeHeader = Sheet.Cells(2, c).Value
                AttributeValue = Sheet.Cells(r, c).Value
                
                If AttributeHeader <> "" Then
                
                    'Replace " char by \" to be valid in Yaml
                    AttributeHeader = Replace(AttributeHeader, """", "\""")
                    AttributeValue = Replace(AttributeValue, """", "\""")
                    
                    Print #Filenum, GLB_INDENT & GLB_INDENT & GLB_INDENT & """" & AttributeHeader & """" & ": """ & AttributeValue & """"
                End If
            Next c
        End If
    Next r
    
    Close #Filenum
    
End Sub

'Export Ranking Guidelines to yml
Private Sub ExportRankingGuidelinesToYml(FilePath, PIRTSheet)

    'Sheet
    Dim Sheet As Worksheet
    Set Sheet = Sheets(PIRTSheet)
    
    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum
    
    'Ranking Guidelines attributes
    RankingGuidelinesTag = "Guidelines"
    DescriptionTag = "Description"
    LevelsTag = "Levels"
        
    'Print Sheetname section
    Print #Filenum, """" & Sheet.Name & """:"
           
    'Print Guidelines section
    Print #Filenum, GLB_INDENT & """" & RankingGuidelinesTag & """:"
     
    'Fields parsing
    AdequacyColumnName = ""
    LastAdequacyColumnName = ""
    AdequacyColumnDescription = ""
    LevelName = ""
    
    Dim rowCount As Long
    rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
    Dim columnCount As Long
    columnCount = Sheet.Cells(2, Sheet.Columns.Count).End(xlToLeft).Column
    
    For r = 1 To rowCount
    
        'Check if the header has been parsed or not
        If LastAdequacyColumnName = "" Then
        
            'If column is not empty, this is not the end of the table
            If Sheet.Range("B" & r).Value <> "" Then
            
                AdequacyColumnName = Sheet.Range("B" & r).Value
            
                'Adequacy Column Name
                Print #Filenum, GLB_INDENT & GLB_INDENT & """" & AdequacyColumnName & """: "
                
                AdequacyColumnDescription = Sheet.Range("C" & r).Value
                AdequacyColumnDescription = Replace(AdequacyColumnDescription, """", "\""")
            
                'Adequacy Column Description
                Print #Filenum, GLB_INDENT & GLB_INDENT & GLB_INDENT & """" & DescriptionTag & """: """ & AdequacyColumnDescription & """"
                
                'Adequacy Column Levels
                Print #Filenum, GLB_INDENT & GLB_INDENT & GLB_INDENT & """" & LevelsTag & """:"
                
            End If
        
        'The header has been parsed so print content
        Else
        
            'If column is not empty, this is not the end of the table
            If Sheet.Range("B" & r).Value <> "" Then
            
                LevelName = Sheet.Range("B" & r).Value
                LevelDescription = Sheet.Range("C" & r).Value
                LevelDescription = Replace(LevelDescription, """", "\""")
            
                'Do not parse the adequacy column header
                If LevelName <> "" And LevelDescription <> "" Then
                    'Adequacy Column Name
                    Print #Filenum, GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & """" & LevelName & """: """ & LevelDescription & """"
                End If
            Else
                AdequacyColumnName = ""
            End If
        End If
            
        'Set the adequacy column to check
        LastAdequacyColumnName = AdequacyColumnName
        
    Next r

End Sub
'Add Level Difference Coloring Cells color values
Private Sub AddLevelDifferenceColoringRgbValues(levelDifferenceColoringSheetName)

    'Sheet
    Dim ShTest1 As Worksheet
    Set ShTest1 = Sheets(levelDifferenceColoringSheetName)
    
    Dim rowCount As Long
    rowCount = ShTest1.UsedRange.Rows.Count
       
    For c = 3 To rowCount
        If ShTest1.Cells(c, 2).Value <> "" Then
            ShTest1.Cells(c, 3).Value = "'" & ConvertColorToRgb(ShTest1.Cells(c, 3).Interior.color)
        End If
    Next c
        
    Range("B" & rowCount).NumberFormat = "@"
    Range("C" & rowCount).NumberFormat = "@"
    
End Sub
'Convert a color to rgb format
Private Function ConvertColorToRgb(color)
    ConvertColorToRgb = (color Mod 256) & "," & ((color \ 256) Mod 256) & "," & (color \ 65536)
End Function
'Delete Level Difference Coloring Cells color values
Private Sub DeleteLevelDifferenceColoringRgbValues(levelDifferenceColoringSheetName)
    Dim ShTest1 As Worksheet
    Set ShTest1 = Sheets(levelDifferenceColoringSheetName)
    
    Dim rowCount As Long
    rowCount = ShTest1.UsedRange.Rows.Count
       
    For c = 3 To rowCount
        ShTest1.Cells(c, 3).Value = ""
    Next c
            
End Sub
