-------------------------------------------------------------------
-- #1098 Error esborrant versions de definicions de procés
-- Modifica les constraints de claus foràrines de jbpm per esborrar en cascada
-------------------------------------------------------------------

ALTER TABLE JBPM_SWIMLANE DROP CONSTRAINT FK_SWL_ASSDEL;

ALTER TABLE JBPM_SWIMLANE ADD
  CONSTRAINT FK_SWL_ASSDEL 
 FOREIGN KEY (ASSIGNMENTDELEGATION_) 
 REFERENCES JBPM_DELEGATION (ID_)
 ON DELETE CASCADE;
